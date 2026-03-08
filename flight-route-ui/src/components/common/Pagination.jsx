export default function Pagination({
    currentPage,
    totalPages,
    onPageChange,
  }) {
    if (totalPages <= 1) return null;
  
    return (
      <div className="pagination">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
        >
          Previous
        </button>
  
        <span>
          Page {currentPage + 1} / {totalPages}
        </span>
  
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage + 1 >= totalPages}
          >
          Next
        </button>
      </div>
    );
  }