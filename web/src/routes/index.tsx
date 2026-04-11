import { createFileRoute } from "@tanstack/react-router";
import { CtaSection } from "~/shared/components/cta-section";
import { EventsSection } from "~/shared/components/event-section";
import { HeroSection } from "~/shared/components/hero-section";
import { HowItWorksSection } from "~/shared/components/how-it-works-section";
import { NGOSection } from "~/shared/components/ngo-section";

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
