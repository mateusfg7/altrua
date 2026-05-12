import { useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import type { ApiError } from "~/shared/types/api-error";
import type { Paginated, PaginationParams } from "~/shared/types/pagination";
import { listEvents } from "../api";
import type { NGOEvent, NGOEventListFilters } from "../types/ngo-event";

export const NGO_EVENT_LIST_QUERY_KEY = ["ngo", "event", "list"];

export function useNGOEventList(
  filters?: PaginationParams & NGOEventListFilters
) {
  return useQuery<Paginated<NGOEvent>, AxiosError<ApiError>>({
    queryKey: [...NGO_EVENT_LIST_QUERY_KEY, filters],
    queryFn: () => listEvents(filters),
  });
}
