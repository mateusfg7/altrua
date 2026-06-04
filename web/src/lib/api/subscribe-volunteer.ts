import { apiClient } from "~/lib/api-client";

type SubscribeVolunteerParams = {
  ongId: string;
  eventId: string;
};

export async function subscribeVolunteer({
  ongId,
  eventId,
}: SubscribeVolunteerParams): Promise<void> {
  await apiClient.post(`/ongs/${ongId}/eventos/${eventId}/volunteers`);
}
