import {
  ArrowRight01Icon,
  Building03Icon,
  UserIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Button } from "~/shared/components/ui/button";

export function CtaSection() {
  return (
    <section className="px-3 py-16">
      <div className="mx-auto grid max-w-6xl gap-6 md:grid-cols-2">
        <div className="relative overflow-hidden rounded-2xl bg-primary p-8 text-primary-foreground md:p-12">
          <div className="absolute -top-10 -right-10 h-40 w-40 rounded-full bg-primary-foreground/10" />
          <div className="absolute -bottom-10 -left-10 h-32 w-32 rounded-full bg-primary-foreground/10" />

          <div className="relative">
            <div className="mb-6 flex size-14 items-center justify-center rounded-xl bg-primary-foreground/20">
              <HugeiconsIcon className="size-7" icon={UserIcon} />
            </div>
            <h3 className="mb-4 font-bold text-2xl md:text-3xl">
              Quero ser voluntário
            </h3>
            <p className="mb-8 text-primary-foreground/80">
              Doe seu tempo e habilidades para causas que você acredita.
              Encontre eventos perto de você e faça parte da mudança.
            </p>
            <Button className="gap-2" size="lg" variant="secondary">
              Criar conta gratuita
              <HugeiconsIcon className="size-4" icon={ArrowRight01Icon} />
            </Button>
          </div>
        </div>

        <div className="relative overflow-hidden rounded-2xl border border-border bg-card p-8 md:p-12">
          <div className="absolute -top-10 -right-10 h-40 w-40 rounded-full bg-primary/5" />
          <div className="absolute -bottom-10 -left-10 h-32 w-32 rounded-full bg-primary/5" />

          <div className="relative">
            <div className="mb-6 flex size-14 items-center justify-center rounded-xl bg-primary/10">
              <HugeiconsIcon
                className="size-7 text-primary"
                icon={Building03Icon}
              />
            </div>
            <h3 className="mb-4 font-bold text-2xl md:text-3xl">Sou uma ONG</h3>
            <p className="mb-8 text-muted-foreground">
              Cadastre sua organização, divulgue seus eventos e conecte-se com
              voluntários e doadores comprometidos.
            </p>
            <Button className="gap-2" size="lg">
              Cadastrar minha ONG
              <HugeiconsIcon className="size-4" icon={ArrowRight01Icon} />
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
}
