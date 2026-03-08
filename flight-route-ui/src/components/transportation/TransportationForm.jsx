import { useState } from "react";
import SimpleAutocomplete from "../common/SimpleAutocomplete";

const TRANSPORTATION_TYPES = ["FLIGHT", "BUS", "SUBWAY", "UBER"];

const DAYS = [
    { label: "Mon", value: "MONDAY" },
    { label: "Tue", value: "TUESDAY" },
    { label: "Wed", value: "WEDNESDAY" },
    { label: "Thu", value: "THURSDAY" },
    { label: "Fri", value: "FRIDAY" },
    { label: "Sat", value: "SATURDAY" },
    { label: "Sun", value: "SUNDAY" }
];


const initialForm = {
    origin: null,
    destination: null,
    transportationType: "FLIGHT",
    operatingDays: [],
};

export default function TransportationForm({
    locations,
    onSubmit,
    loading,
}) {
    const [form, setForm] = useState(initialForm);

    const handleChange = (e) => {
        const { name, value } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleDayToggle = (dayValue) => {
        setForm((prev) => {
            const exists = prev.operatingDays.includes(dayValue);

            return {
                ...prev,
                operatingDays: exists
                    ? prev.operatingDays.filter((d) => d !== dayValue)
                    : [...prev.operatingDays, dayValue].sort((a, b) => a - b),
            };
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!form.origin || !form.destination) {
            alert("Origin ve destination seçmelisin");
            return;
        }

        onSubmit(
            {
                origin: form.origin,
                destination: form.destination,
                transportationType: form.transportationType,
                operatingDays: form.operatingDays,
            },
            () => setForm(initialForm)
        );
    };

    return (
        <div className="crud-card">
            <h3>Create Transportation</h3>

            <form className="crud-form" onSubmit={handleSubmit}>
                <SimpleAutocomplete
                    label="Origin Location"
                    placeholder="Origin ara..."
                    options={locations}
                    value={form.origin}
                    disabled={loading}
                    onSelect={(item) =>
                        setForm((prev) => ({
                            ...prev,
                            origin: item || null,
                        }))
                    }
                />

                <SimpleAutocomplete
                    label="Destination Location"
                    placeholder="Destination ara..."
                    options={locations}
                    value={form.destination}
                    disabled={loading}
                    onSelect={(item) =>
                        setForm((prev) => ({
                            ...prev,
                            destination: item || null,
                        }))
                    }
                />

                <div className="form-group">
                    <label>Transportation Type</label>
                    <select
                        name="transportationType"
                        value={form.transportationType}
                        onChange={handleChange}
                        disabled={loading}
                    >
                        {TRANSPORTATION_TYPES.map((type) => (
                            <option key={type} value={type}>
                                {type}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-group full-width">
                    <label>Operating Days</label>
                    <div className="days-grid">
                        {DAYS.map((day) => (
                            <label key={day.value} className="day-checkbox">
                                <input
                                    type="checkbox"
                                    checked={form.operatingDays.includes(day.value)}
                                    onChange={() => handleDayToggle(day.value)}
                                    disabled={loading}
                                />
                                <span>{day.label}</span>
                            </label>
                        ))}
                    </div>
                </div>

                <div className="crud-actions">
                    <button type="submit" disabled={loading}>
                        {loading ? "Saving..." : "Create"}
                    </button>
                </div>
            </form>
        </div>
    );
}