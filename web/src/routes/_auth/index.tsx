import { createFileRoute } from "@tanstack/react-router";
import { CreateNgoDialog } from "~/components/create-ngo-dialog";
import { EventDetails } from "~/components/event-details";
import { Feed } from "~/components/feed";
import { CtaSection } from "~/components/lp/cta-section";
import { EventsSection } from "~/components/lp/event-section";
import { HeroSection } from "~/components/lp/hero-section";
import { HowItWorksSection } from "~/components/lp/how-it-works-section";
import { NGOSection } from "~/components/lp/ngo-section";
import { OngDetails } from "~/components/ong-details";
import { useAuthStore } from "~/store/auth.store";

export const Route = createFileRoute("/_auth/")({
  component: RouteComponent,
});

function RouteComponent() {
  const accessToken = useAuthStore((s) => s.accessToken);

  if (accessToken) {
    return (
      <>
        <Feed />

        <OngDetails />
        <EventDetails />
        <CreateNgoDialog />
      </>
    );
  }

  return (
    <div className="space-y-20">
      <HeroSection />
      <NGOSection />
      <EventsSection />
      <HowItWorksSection />
      <CtaSection />

      <OngDetails />
      <EventDetails />
      <CreateNgoDialog />
    </div>
  );
}
