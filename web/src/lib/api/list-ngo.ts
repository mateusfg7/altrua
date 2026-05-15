import { apiClient } from "~/lib/api-client";
import type { NGO } from "~/types/ngo";
import type { Paginated, PaginationParams } from "~/types/pagination";

export async function listNGOs(
  pagination?: PaginationParams
): Promise<Paginated<NGO>> {
  return await apiClient
    .get<Paginated<NGO>>("/ongs", { params: pagination })
    .then((res) => res.data);
}
