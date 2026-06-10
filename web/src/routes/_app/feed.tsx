import { createFileRoute } from "@tanstack/react-router";
import { CreateNgoDialog } from "~/components/create-ngo-dialog";
import { EventDetails } from "~/components/event-details";
import { Feed } from "~/components/feed";
import { OngDetails } from "~/components/ong-details";

export const Route = createFileRoute("/_app/feed")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <>
      <Feed />
      <OngDetails />
      <EventDetails />
      <CreateNgoDialog />
    </>
  );
}
