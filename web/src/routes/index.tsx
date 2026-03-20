import { createFileRoute } from "@tanstack/react-router";
import { EventsSection } from "~/components/event-section";
import { HeroSection } from "~/components/hero-section";
import { OngSection } from "~/components/ong-section";

export const Route = createFileRoute("/")({ component: App });

function App() {
  return (
    <div className="space-y-20">
      <HeroSection />
      <OngSection />
      <EventsSection />
    </div>
  );
}
