import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { subscribeVolunteer } from "~/lib/api/subscribe-volunteer";
import type { ApiError } from "~/types/api-error";
import { NGO_EVENT_LIST_QUERY_KEY } from "./use-ngo-event-list";

type SubscribeVolunteerParams = {
  ongId: string;
  eventId: string;
};

export function useVolunteerSubscription() {
  const queryClient = useQueryClient();

  return useMutation<void, AxiosError<ApiError>, SubscribeVolunteerParams>({
    mutationFn: subscribeVolunteer,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: NGO_EVENT_LIST_QUERY_KEY });
    },
  });
}
