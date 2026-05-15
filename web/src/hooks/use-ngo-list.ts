import { useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listNGOs } from "~/lib/api/list-ngo";
import type { ApiError } from "~/types/api-error";
import type { Paginated, PaginationParams } from "~/types/pagination";
import type { Ngo } from "../types/ngo";

export const NGO_LIST_QUERY_KEY = ["ngo", "list"];

export function useNgoList(pagination?: PaginationParams) {
  return useQuery<Paginated<Ngo>, AxiosError<ApiError>>({
    queryKey: [...NGO_LIST_QUERY_KEY, pagination],
    queryFn: () => listNGOs(pagination),
    retry: false,
  });
}
