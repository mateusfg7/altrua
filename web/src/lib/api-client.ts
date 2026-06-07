import axios, { type AxiosRequestConfig } from "axios";
import { refreshTokens } from "~/lib/api/refresh";
import { useAuthStore } from "~/store/auth.store";

/**
 * Same-origin base path. Requests to `/api/*` are proxied to the backend
 * (see vercel.json rewrites in prod, vite.config.ts proxy in dev), which
 * avoids mixed-content blocking when the page is served over HTTPS.
 */
export const API_BASE_PATH = "/api";

export const apiClient = axios.create({
  baseURL: API_BASE_PATH,
});

// Attach access token to every request
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 401 → refresh → retry once
let isRefreshing = false;
let queue: Array<{
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}> = [];

function processQueue(error: unknown, token: string | null) {
  for (const p of queue) {
    if (error) {
      p.reject(error);
    } else {
      p.resolve(token ?? "");
    }
  }
  queue = [];
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original: AxiosRequestConfig & { _retry?: boolean } = error.config;

    if (error.response?.status !== 401 || original._retry) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      // Queue concurrent requests while a refresh is in flight
      return new Promise((resolve, reject) => {
        queue.push({ resolve, reject });
      }).then((token) => {
        original.headers = {
          ...original.headers,
          Authorization: `Bearer ${token}`,
        };
        return apiClient(original);
      });
    }

    original._retry = true;
    isRefreshing = true;

    const { getRefreshToken, setTokens, clearTokens } = useAuthStore.getState();
    const refreshToken = getRefreshToken();

    if (!refreshToken) {
      clearTokens();
      processQueue(error, null);
      isRefreshing = false;

      return Promise.reject(error);
    }

    try {
      const tokens = await refreshTokens(refreshToken);
      setTokens(tokens);
      processQueue(null, tokens.accessToken);
      original.headers = {
        ...original.headers,
        Authorization: `Bearer ${tokens.accessToken}`,
      };
      return apiClient(original);
    } catch (refreshError) {
      clearTokens();
      processQueue(refreshError, null);

      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);
