import { useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import type { ApiError } from "~/shared/types/api-error";
import type { Paginated, PaginationParams } from "~/shared/types/pagination";
import { listNGOs } from "../api";
import type { NGO } from "../types/ngo";

export const NGO_LIST_QUERY_KEY = ["ngo", "list"];

export function useNgoList(pagination?: PaginationParams) {
  return useQuery<Paginated<NGO>, AxiosError<ApiError>>({
    queryKey: [...NGO_LIST_QUERY_KEY, pagination],
    queryFn: () => listNGOs(pagination),
    retry: false,
  });
}
