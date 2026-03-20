import {
  Cancel01Icon,
  FavouriteIcon,
  Login01Icon,
  Menu09Icon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Link } from "@tanstack/react-router";
import React from "react";
import { Button } from "../ui/button";

export function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false);

  const nav = [
    {
      name: "ONGs",
      href: "#ongs",
    },
    {
      name: "Eventos",
      href: "#eventos",
    },
    {
      name: "Como Funciona",
      href: "#como-funciona",
    },
  ];

  return (
    <header className="sticky top-0 border-muted border-b bg-background/95 p-3 backdrop-blur-md supports-backdrop-filter:bg-background/60">
      <div className="mx-auto flex max-w-6xl grid-cols-4 items-center justify-between gap-1 md:grid">
        <Link className="flex items-center gap-2" to="/">
          <div className="flex size-9 items-center justify-center rounded-lg bg-primary">
            <HugeiconsIcon
              className="size-5 text-primary-foreground"
              icon={FavouriteIcon}
            />
          </div>
          <span className="font-bold text-xl tracking-tight">Altrua</span>
        </Link>

        <nav className="col-span-2 hidden items-center justify-center gap-6 md:flex">
          {nav.map((item) => (
            <a
              className="font-medium text-muted-foreground text-sm transition-colors hover:text-foreground"
              href={item.href}
              key={item.name}
            >
              {item.name}
            </a>
          ))}
        </nav>

        <div className="hidden items-center justify-end gap-3 md:flex">
          <Button size="sm" variant="ghost">
            <HugeiconsIcon icon={Login01Icon} />
            Entrar
          </Button>
          <Button size="sm">Cadastre-se</Button>
        </div>

        <Button
          className="md:hidden"
          onClick={() => setMobileMenuOpen((prev) => !prev)}
          size="icon-lg"
          variant="ghost"
        >
          <HugeiconsIcon
            className="size-6"
            icon={mobileMenuOpen ? Cancel01Icon : Menu09Icon}
          />
        </Button>
      </div>

      {mobileMenuOpen && (
        <div className="mt-4 border-border border-t md:hidden">
          <nav className="container mx-auto flex flex-col gap-4 px-4 py-4">
            {nav.map((item) => (
              <a
                className="font-medium text-muted-foreground text-sm transition-colors hover:text-foreground"
                href={item.href}
                key={item.name}
                onClick={() => setMobileMenuOpen(false)}
              >
                {item.name}
              </a>
            ))}

            <div className="flex flex-col gap-2 pt-2">
              <Button className="w-full justify-center py-4" size="sm">
                Cadastre-se
              </Button>
              <Button
                className="w-full justify-center py-4"
                size="sm"
                variant="ghost"
              >
                <HugeiconsIcon icon={Login01Icon} />
                Entrar
              </Button>
            </div>
          </nav>
        </div>
      )}
    </header>
  );
}
