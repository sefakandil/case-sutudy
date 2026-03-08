import { useEffect, useMemo, useRef, useState } from "react";

export default function SimpleAutocomplete({
  label,
  placeholder,
  options,
  value,
  onSelect,
  disabled,
}) {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const wrapperRef = useRef(null);

  useEffect(() => {
    if (!value) {
      setQuery("");
      return;
    }

    setQuery(`${value.name} (${value.locationCode})`);
  }, [value]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setOpen(false);

        if (!value) {
          setQuery("");
        }
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [value]);

  const filteredOptions = useMemo(() => {
    const search = query.toLowerCase().trim();

    if (!search) return options.slice(0, 8);

    return options
      .filter((item) => {
        const text =
          `${item.name} ${item.city} ${item.country} ${item.locationCode}`.toLowerCase();
        return text.includes(search);
      })
      .slice(0, 8);
  }, [options, query]);

  const handleSelect = (item) => {
    onSelect(item);
    setQuery(`${item.name} (${item.locationCode})`);
    setOpen(false);
  };

  return (
    <div className="form-group autocomplete-wrapper" ref={wrapperRef}>
      <label>{label}</label>

      <input
        type="text"
        value={query}
        placeholder={placeholder}
        disabled={disabled}
        onFocus={() => setOpen(true)}
        onChange={(e) => {
          setQuery(e.target.value);
          setOpen(true);
          onSelect(null);
        }}
      />

      {open && filteredOptions.length > 0 && (
        <div className="autocomplete-dropdown">
          {filteredOptions.map((item) => (
            <button
              type="button"
              key={item.id}
              className="autocomplete-item"
              onClick={() => handleSelect(item)}
            >
              <div className="autocomplete-main">
                {item.name} ({item.locationCode})
              </div>
              <div className="autocomplete-sub">
                {item.city}, {item.country}
              </div>
            </button>
          ))}
        </div>
      )}

      {open && query && filteredOptions.length === 0 && (
        <div className="autocomplete-dropdown">
          <div className="autocomplete-empty">Sonuç bulunamadı</div>
        </div>
      )}
    </div>
  );
}