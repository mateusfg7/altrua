import { useFeedStore } from "~/store/feed.store";
import { EventFeed } from "./event-feed";
import { NgoFeed } from "./ngo-feed";

const SECTION_META = {
  eventos: {
    title: "Eventos",
    description: "Encontre oportunidades para ajudar perto de você.",
  },
  ongs: {
    title: "ONGs",
    description: "Conheça as organizações que estão fazendo a diferença.",
  },
} as const;

export function Feed() {
  const section = useFeedStore((s) => s.section);
  const meta = SECTION_META[section];

  return (
    <section className="mx-auto w-full max-w-5xl px-4 py-8">
      <header className="mb-6">
        <h1 className="font-bold text-2xl tracking-tight">{meta.title}</h1>
        <p className="text-muted-foreground text-sm">{meta.description}</p>
      </header>

      {section === "eventos" ? <EventFeed /> : <NgoFeed />}
    </section>
  );
}
