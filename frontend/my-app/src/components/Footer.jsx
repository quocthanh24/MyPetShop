const Footer = () => {
  return (
    <footer className="bg-gray-800 text-white mt-auto">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-xl font-bold mb-4">Pet Clinic</h3>
            <p className="text-gray-400">
              Chăm sóc thú cưng của bạn với tình yêu và chuyên môn.
            </p>
          </div>
          <div>
            <h3 className="text-xl font-bold mb-4">Liên hệ</h3>
            <p className="text-gray-400">Email: info@petclinic.com</p>
            <p className="text-gray-400">Phone: 0123 456 789</p>
          </div>
          <div>
            <h3 className="text-xl font-bold mb-4">Giờ làm việc</h3>
            <p className="text-gray-400">Thứ 2 - Chủ nhật: 8:00 - 20:00</p>
          </div>
        </div>
        <div className="border-t border-gray-700 mt-8 pt-8 text-center text-gray-400">
          <p>&copy; 2024 Pet Clinic. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

