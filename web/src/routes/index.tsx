import { createFileRoute } from "@tanstack/react-router";
import { CtaSection } from "~/components/lp/cta-section";
import { EventsSection } from "~/components/lp/event-section";
import { HeroSection } from "~/components/lp/hero-section";
import { HowItWorksSection } from "~/components/lp/how-it-works-section";
import { NGOSection } from "~/components/lp/ngo-section";

export const Route = createFileRoute("/")({ component: App });

function App() {
  return (
    <div className="space-y-20">
      <HeroSection />
      <NGOSection />
      <EventsSection />
      <HowItWorksSection />
      <CtaSection />
    </div>
  );
}
