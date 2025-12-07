import { useState, useEffect } from 'react';

// Component to display product images with error handling
// Uses backend endpoint instead of direct thumbnailUrl access
const ProductImage = ({ productId, productName, size = 'medium' }) => {
  const [imageError, setImageError] = useState(false);
  const [imageSrc, setImageSrc] = useState(null);
  const [loading, setLoading] = useState(true);

  // Determine dimensions based on size prop
  const sizeClasses = {
    small: 'w-16 h-16',
    medium: 'w-full h-full',
    large: 'w-64 h-64',
  };

  useEffect(() => {
    if (!productId) {
      setLoading(false);
      return;
    }

    // Load image with authentication token
    const loadImage = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          setImageError(true);
          setLoading(false);
          return;
        }

        // Try both customer and employee endpoints
        let response;
        try {
          response = await fetch(
            `http://localhost:8080/api/customers/products/${productId}/thumbnail`,
            {
              headers: {
                'Authorization': `Bearer ${token}`,
              },
            }
          );
        } catch (err) {
          // Fallback to employee endpoint if customer endpoint fails
          response = await fetch(
            `http://localhost:8080/api/employees/products/${productId}/thumbnail`,
            {
              headers: {
                'Authorization': `Bearer ${token}`,
              },
            }
          );
        }

        if (!response.ok) {
          throw new Error('Failed to load image');
        }

        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);
        setImageSrc(blobUrl);
        setLoading(false);
      } catch (error) {
        console.error('Image load error for product:', productId, error);
        setImageError(true);
        setLoading(false);
      }
    };

    loadImage();

    // Cleanup blob URL when component unmounts
    return () => {
      if (imageSrc) {
        URL.revokeObjectURL(imageSrc);
      }
    };
  }, [productId, imageSrc]);

  if (!productId) {
    return (
      <div className={`${sizeClasses[size]} bg-gray-200 rounded flex items-center justify-center`}>
        <span className="text-gray-400 text-sm">No Image</span>
      </div>
    );
  }

  if (loading) {
    return (
      <div className={`${sizeClasses[size]} bg-gray-200 rounded flex items-center justify-center`}>
        <span className="text-gray-400 text-sm">Loading...</span>
      </div>
    );
  }

  if (imageError || !imageSrc) {
    return (
      <div className={`${sizeClasses[size]} bg-gray-200 rounded flex items-center justify-center`}>
        <span className="text-gray-400 text-sm">No Image</span>
      </div>
    );
  }

  return (
    <img
      src={imageSrc}
      alt={productName || 'Product image'}
      className={`${sizeClasses[size]} object-cover rounded`}
    />
  );
};

export default ProductImage;
