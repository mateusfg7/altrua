import { queryOptions, useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { apiClient } from "~/lib/api-client";
import { useAuthStore } from "~/store/auth.store";
import type { ApiError } from "~/types/api-error";

export type UserProfile = {
  id: string;
  name: string;
  email: string;
  // extend as needed
};

async function fetchProfile(): Promise<UserProfile> {
  return await apiClient.get<UserProfile>("/users/me").then((res) => res.data);
}

export const profileQueryOptions = queryOptions({
  queryKey: ["profile"],
  queryFn: fetchProfile,
  staleTime: 1000 * 60 * 5,
  retry: false,
});

export function useProfile() {
  const accessToken = useAuthStore((s) => s.accessToken);

  return useQuery<UserProfile, AxiosError<ApiError>>({
    ...profileQueryOptions,
    enabled: !!accessToken,
  });
}
