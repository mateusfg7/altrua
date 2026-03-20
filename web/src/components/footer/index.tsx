// import {
//   FavouriteIcon,
//   InstagramIcon,
//   Linkedin01Icon,
//   Mail01Icon,
//   NewTwitterIcon,
// } from "hugeicons-react";

import {
  FavouriteIcon,
  InstagramIcon,
  Linkedin01Icon,
  Mail01Icon,
  NewTwitterIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { Link } from "@tanstack/react-router";

const footerLinks = {
  plataforma: [
    { label: "Sobre nós", href: "#" },
    { label: "Como funciona", href: "#como-funciona" },
    { label: "ONGs parceiras", href: "#ongs" },
    { label: "Blog", href: "#" },
  ],
  recursos: [
    { label: "Central de ajuda", href: "#" },
    { label: "Guia do voluntário", href: "#" },
    { label: "Para ONGs", href: "#" },
    { label: "API", href: "#" },
  ],
  legal: [
    { label: "Termos de uso", href: "#" },
    { label: "Privacidade", href: "#" },
    { label: "Cookies", href: "#" },
  ],
};

const socialLinks = [
  { icon: InstagramIcon, href: "#", label: "Instagram" },
  { icon: NewTwitterIcon, href: "#", label: "Twitter" },
  { icon: Linkedin01Icon, href: "#", label: "LinkedIn" },
  { icon: Mail01Icon, href: "#", label: "Email" },
];

export function Footer() {
  return (
    <footer className="border-border border-t bg-muted/30 px-3 py-16">
      <div className="mx-auto max-w-6xl">
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-5">
          <div className="lg:col-span-2">
            <Link className="mb-4 flex items-center gap-2" to="/">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary">
                <HugeiconsIcon
                  className="size-5 text-primary-foreground"
                  icon={FavouriteIcon}
                />
              </div>
              <span className="font-bold text-xl tracking-tight">Altrua</span>
            </Link>
            <p className="mb-6 max-w-sm text-muted-foreground text-sm">
              Conectando quem quer ajudar com quem precisa. Transforme sua
              vontade de fazer o bem em ação real.
            </p>
            <div className="flex gap-3">
              {socialLinks.map((social) => (
                <Link
                  aria-label={social.label}
                  className="flex size-10 items-center justify-center rounded-lg bg-muted text-muted-foreground transition-colors hover:bg-primary hover:text-primary-foreground"
                  key={social.label}
                  to={social.href}
                >
                  <HugeiconsIcon className="size-5" icon={social.icon} />
                </Link>
              ))}
            </div>
          </div>

          <div>
            <h4 className="mb-4 font-semibold">Plataforma</h4>
            <ul className="space-y-3">
              {footerLinks.plataforma.map((link) => (
                <li key={link.label}>
                  <Link
                    className="text-muted-foreground text-sm transition-colors hover:text-foreground"
                    to={link.href}
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="mb-4 font-semibold">Recursos</h4>
            <ul className="space-y-3">
              {footerLinks.recursos.map((link) => (
                <li key={link.label}>
                  <Link
                    className="text-muted-foreground text-sm transition-colors hover:text-foreground"
                    to={link.href}
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="mb-4 font-semibold">Legal</h4>
            <ul className="space-y-3">
              {footerLinks.legal.map((link) => (
                <li key={link.label}>
                  <Link
                    className="text-muted-foreground text-sm transition-colors hover:text-foreground"
                    to={link.href}
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="mt-12 flex flex-col items-center justify-between gap-4 border-border border-t pt-8 md:flex-row">
          <p className="text-muted-foreground text-sm">
            © 2026 Altrua. Todos os direitos reservados.
          </p>
          <p className="flex items-center gap-1 text-muted-foreground text-sm">
            Feito com{" "}
            <HugeiconsIcon
              className="size-4 text-primary"
              icon={FavouriteIcon}
            />{" "}
            para um mundo melhor
          </p>
        </div>
      </div>
    </footer>
  );
}
