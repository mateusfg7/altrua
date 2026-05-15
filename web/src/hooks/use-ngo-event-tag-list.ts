import { useQuery } from "@tanstack/react-query";
import { listEventTags } from "~/lib/api/list-event-tags";

export function useNgoEventTagList() {
  return useQuery({
    queryKey: ["ngo", "event", "tag", "list"],
    queryFn: listEventTags,
  });
}
