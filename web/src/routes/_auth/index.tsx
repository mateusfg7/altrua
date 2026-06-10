import { createFileRoute, redirect } from "@tanstack/react-router";
import { CreateNgoDialog } from "~/components/create-ngo-dialog";
import { EventDetails } from "~/components/event-details";
import { CtaSection } from "~/components/lp/cta-section";
import { EventsSection } from "~/components/lp/event-section";
import { HeroSection } from "~/components/lp/hero-section";
import { HowItWorksSection } from "~/components/lp/how-it-works-section";
import { NGOSection } from "~/components/lp/ngo-section";
import { OngDetails } from "~/components/ong-details";

export const Route = createFileRoute("/_auth/")({
  beforeLoad: ({ context }) => {
    if (context.isAuthenticated) {
      throw redirect({ to: "/feed" });
    }
  },
  component: RouteComponent,
});

function RouteComponent() {
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
