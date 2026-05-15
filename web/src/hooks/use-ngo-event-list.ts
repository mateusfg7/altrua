import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { listEvents } from "~/lib/api/list-event";
import type { ApiError } from "~/types/api-error";
import type { Paginated, PaginationParams } from "~/types/pagination";
import type { NgoEvent, NgoEventListFilters } from "../types/ngo-event";

export const NGO_EVENT_LIST_QUERY_KEY = ["ngo", "event", "list"];

export function useNgoEventList(
  filters?: PaginationParams & NgoEventListFilters
) {
  return useQuery<Paginated<NgoEvent>, AxiosError<ApiError>>({
    queryKey: [...NGO_EVENT_LIST_QUERY_KEY, filters],
    queryFn: () => listEvents(filters),
    placeholderData: keepPreviousData,
    refetchOnWindowFocus: false,
  });
}
