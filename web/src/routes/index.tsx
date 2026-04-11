import { createFileRoute } from "@tanstack/react-router";
import { CtaSection } from "~/shared/components/lp/cta-section";
import { EventsSection } from "~/shared/components/lp/event-section";
import { HeroSection } from "~/shared/components/lp/hero-section";
import { HowItWorksSection } from "~/shared/components/lp/how-it-works-section";
import { NGOSection } from "~/shared/components/lp/ngo-section";

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
