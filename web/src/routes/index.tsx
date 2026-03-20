import { createFileRoute } from "@tanstack/react-router";
import { HeroSection } from "~/components/hero-section";

export const Route = createFileRoute("/")({ component: App });

function App() {
  return (
    <div>
      <HeroSection />
    </div>
  );
}
