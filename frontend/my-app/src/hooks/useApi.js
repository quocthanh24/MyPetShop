import { useState, useEffect } from 'react';
import axios from 'axios';

/**
 * Custom hook để fetch data từ API
 * @param {string} url - API endpoint
 * @returns {Object} { data, loading, error }
 */
export const useApi = (url) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem('accessToken');
        const response = await axios.get(url, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        setData(response.data);
        setError(null);
      } catch (err) {
        setError(err.response?.data?.message || err.message);
        setData(null);
      } finally {
        setLoading(false);
      }
    };

    if (url) {
      fetchData();
    }
  }, [url]);

  return { data, loading, error };
};
