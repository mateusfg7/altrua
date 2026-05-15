import type { NgoEventTag } from "~/types/ngo-event-tag";
import { apiClient } from "../api-client";

export async function listEventTags() {
  return await apiClient.get<NgoEventTag[]>("/tags").then((res) => res.data);
}
