import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

/**
 * Defines the structure and actions for the authentication state.
 */
type AuthState = {
  /**
   * Short-lived access token.
   * Kept strictly in memory for security and is never persisted to storage.
   * @type {string | null}
   */
  accessToken: string | null;

  /**
   * Long-lived refresh token.
   * Persisted to local storage to maintain sessions across reloads.
   * @type {string | null}
   */
  refreshToken: string | null;

  /**
   * Updates both the access and refresh tokens in the state.
   * The persist middleware automatically captures and saves the refresh token.
   *
   * @param {Object} tokens - The authentication tokens.
   * @param {string} tokens.accessToken - The new JWT access token.
   * @param {string} tokens.refreshToken - The new JWT refresh token.
   */
  setTokens: (tokens: { accessToken: string; refreshToken: string }) => void;

  /**
   * Clears both tokens from the state, effectively logging the user out.
   * The persist middleware automatically removes the refresh token from storage.
   */
  clearTokens: () => void;

  /**
   * Retrieves the current refresh token directly from the state.
   *
   * @returns {string | null} The stored refresh token, or null if unauthenticated.
   */
  getRefreshToken: () => string | null;
};

/**
 * Zustand store for managing authentication tokens.
 * Uses the `persist` middleware to safely handle SSR environments and persist ONLY the refresh token.
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      refreshToken: null,

      setTokens: ({ accessToken, refreshToken }) => {
        console.log("Set tokens...");
        set({ accessToken, refreshToken });
      },

      clearTokens: () => {
        set({ accessToken: null, refreshToken: null });
      },

      getRefreshToken: () => get().refreshToken,
    }),
    {
      name: "auth-storage",
      storage: createJSONStorage(() => localStorage),
      /**
       * Filters the state before saving to storage.
       * Only the refresh token is written to localStorage.
       *
       * @param {AuthState} state - The current state.
       * @returns {Partial<AuthState>} The subset of state to persist.
       */
      partialize: (state: AuthState): Partial<AuthState> => ({
        refreshToken: state.refreshToken,
      }),
    }
  )
);
