const Card = ({ children, className = '', onClick }) => {
  return (
    <div
      className={`
        bg-white rounded-lg shadow-md p-6 transition hover:shadow-lg
        ${onClick ? 'cursor-pointer' : ''}
        ${className}
      `}
      onClick={onClick}
    >
      {children}
    </div>
  );
};

export default Card;

