// ✅ REFACTORED IMPORT EXAMPLES

// ============ OLD IMPORTS ❌ ============
// import Button from '../../../../components/Button';
// import Card from '../../../../components/Card';
// import Modal from '../../../../components/Modal';
// import Input from '../../../../components/Input';
// import { useAuth } from '../context/AuthContext';
// import { customerProductAPI, cartAPI, orderAPI } from '../services/api';

// ============ NEW IMPORTS ✅ ============
import { Button, Card, Modal, Input, ProductImage } from '@/components';
import { Header, Footer } from '@/components/layout';
import { useAuth } from '@/context/AuthContext';
import { customerProductAPI, cartAPI, orderAPI, paymentAPI } from '@/services/api';
import { useApi } from '@/hooks';
import { formatPrice, validateEmail } from '@/utils';

// ============ EXAMPLE PAGE STRUCTURE ============

export default function ExamplePage() {
  const { user, isAuthenticated } = useAuth();
  const { data: products, loading, error } = useApi('/api/customers/products');

  return (
    <div>
      <Header />
      <main>
        <Card>
          <h1>Example Component</h1>
          <Button>Click me</Button>
          <Input type="email" placeholder="Email" />
          <Modal isOpen={false} title="Modal Example">
            Content
          </Modal>
        </Card>
      </main>
      <Footer />
    </div>
  );
}
