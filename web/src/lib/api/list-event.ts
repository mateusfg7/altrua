import type { NGOEvent, NGOEventListFilters } from "~/types/ngo-event";
import type { Paginated, PaginationParams } from "~/types/pagination";
import { apiClient } from "../api-client";

export async function listEvents(
  pagination?: PaginationParams & NGOEventListFilters
) {
  return await apiClient
    .get<Paginated<NGOEvent>>("/eventos", { params: pagination })
    .then((res) => res.data);
}
