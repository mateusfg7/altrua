import { createFileRoute } from "@tanstack/react-router";
import { CtaSection } from "~/components/cta-section";
import { EventsSection } from "~/components/event-section";
import { HeroSection } from "~/components/hero-section";
import { HowItWorksSection } from "~/components/how-it-works-section";
import { NGOSection } from "~/components/ngo-section";

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
