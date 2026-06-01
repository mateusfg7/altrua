import { refreshTokens } from "~/lib/api/refresh";
import { useAuthStore } from "~/store/auth.store";

export async function bootstrapAuth(): Promise<void> {
  const { getRefreshToken, setTokens } = useAuthStore.getState();
  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    return;
  }

  try {
    const tokens = await refreshTokens(refreshToken);
    setTokens(tokens);
  } catch {
    useAuthStore.getState().clearTokens();
  }
}
