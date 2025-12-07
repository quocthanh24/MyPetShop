import { Link } from 'react-router-dom';
import Card from '../components/Card';
import Button from '../components/Button';

const HomePage = () => {
  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-500 to-primary-700 text-white py-20">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-5xl font-bold mb-4">Chào mừng đến với Pet Clinic</h1>
          <p className="text-xl mb-8">Chăm sóc thú cưng của bạn với tình yêu và chuyên môn</p>
          <div className="flex justify-center space-x-4">
            <Link to="/customer?tab=products">
              <Button variant="secondary">Xem Sản Phẩm</Button>
            </Link>
            <Link to="/appointments">
              <Button variant="secondary">Xem Lịch Hẹn</Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">Dịch Vụ Của Chúng Tôi</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Card>
              <div className="text-center">
                <div className="text-5xl mb-4">🏥</div>
                <h3 className="text-xl font-bold mb-2">Khám Bệnh</h3>
                <p className="text-gray-600">
                  Dịch vụ khám bệnh chuyên nghiệp cho thú cưng của bạn
                </p>
              </div>
            </Card>
            <Card>
              <div className="text-center">
                <div className="text-5xl mb-4">💊</div>
                <h3 className="text-xl font-bold mb-2">Sản Phẩm Y Tế</h3>
                <p className="text-gray-600">
                  Cung cấp các sản phẩm y tế chất lượng cao cho thú cưng
                </p>
              </div>
            </Card>
            <Card>
              <div className="text-center">
                <div className="text-5xl mb-4">📅</div>
                <h3 className="text-xl font-bold mb-2">Xem Lịch Hẹn</h3>
                <p className="text-gray-600">
                  Xem và quản lý lịch hẹn của bạn một cách dễ dàng và tiện lợi
                </p>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Về Chúng Tôi</h2>
            <p className="text-gray-600 text-lg">
              Pet Clinic là phòng khám thú y hiện đại với đội ngũ bác sĩ giàu kinh nghiệm
              và trang thiết bị y tế tiên tiến. Chúng tôi cam kết mang đến dịch vụ chăm sóc
              tốt nhất cho thú cưng của bạn.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;

