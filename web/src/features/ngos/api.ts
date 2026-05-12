import { apiClient } from "~/shared/lib/api-client";
import type { Paginated, PaginationParams } from "~/shared/types/pagination";
import type { NGO } from "./types/ngo";
import type { NGOEvent, NGOEventListFilters } from "./types/ngo-event";

export async function listNGOs(
  pagination?: PaginationParams
): Promise<Paginated<NGO>> {
  return await apiClient
    .get<Paginated<NGO>>("/ongs", { params: pagination })
    .then((res) => res.data);
}

export async function listEvents(
  pagination?: PaginationParams & NGOEventListFilters
) {
  return await apiClient
    .get<Paginated<NGOEvent>>("/eventos", { params: pagination })
    .then((res) => res.data);
}
