import { apiClient } from "~/lib/api-client";
import type { Paginated } from "~/types/paginated";
import type { NGO } from "./types/ngo";

export async function listNGOs() {
  return await apiClient.get<Paginated<NGO>>("/ongs").then((res) => res.data);
}
