import {
  Calendar03Icon,
  FavouriteIcon,
  Search01Icon,
  UserAdd01Icon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";

const steps = [
  {
    icon: Search01Icon,
    title: "Explore",
    description:
      "Navegue por centenas de ONGs e eventos. Filtre por área de atuação, localização ou tipo de contribuição.",
  },
  {
    icon: UserAdd01Icon,
    title: "Cadastre-se",
    description:
      "Crie sua conta gratuita em poucos minutos. É rápido, seguro e você pode começar a ajudar imediatamente.",
  },
  {
    icon: Calendar03Icon,
    title: "Participe",
    description:
      "Inscreva-se em eventos de voluntariado ou contribua com doações. Você escolhe como quer ajudar.",
  },
  {
    icon: FavouriteIcon,
    title: "Transforme",
    description:
      "Acompanhe seu impacto e veja como sua contribuição está transformando vidas e comunidades.",
  },
];

export function HowItWorksSection() {
  return (
    <section
      className="bg-foreground px-3 py-20 text-background"
      id="como-funciona"
    >
      <div className="mx-auto max-w-6xl space-y-20">
        <div className="text-center">
          <h2 className="font-bold text-3xl tracking-tight md:text-4xl">
            Como Funciona
          </h2>
          <p className="mx-auto mt-4 max-w-2xl text-background/70">
            Participar é simples. Em poucos passos você estará conectado a
            causas que importam.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-2 2xl:grid-cols-4">
          {steps.map((step, index) => (
            <div className="relative" key={step.title}>
              <div className="flex flex-col items-center text-center">
                <div className="relative mb-6">
                  <div className="flex size-16 items-center justify-center rounded-2xl bg-primary">
                    <HugeiconsIcon
                      className="size-8 text-primary-foreground"
                      icon={step.icon}
                    />
                  </div>
                  <div className="absolute -top-2 -right-2 flex size-8 items-center justify-center rounded-full bg-background font-bold text-foreground text-sm">
                    {index + 1}
                  </div>
                </div>
                <h3 className="mb-3 font-semibold text-xl">{step.title}</h3>
                <p className="text-balance text-background/70 text-sm">
                  {step.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
