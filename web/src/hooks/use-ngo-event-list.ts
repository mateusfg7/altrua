import { useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listEvents } from "~/lib/api/list-event";
import type { ApiError } from "~/types/api-error";
import type { Paginated, PaginationParams } from "~/types/pagination";
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
