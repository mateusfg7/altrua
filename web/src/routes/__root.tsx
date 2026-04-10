import { TanStackDevtools } from "@tanstack/react-devtools";
import type { QueryClient } from "@tanstack/react-query";
import {
  createRootRouteWithContext,
  HeadContent,
  Scripts,
} from "@tanstack/react-router";
import { TanStackRouterDevtoolsPanel } from "@tanstack/react-router-devtools";
import { Footer } from "~/components/footer";
import { Header } from "~/components/header";
import TanStackQueryDevtools from "../integrations/tanstack-query/devtools";
import TanStackQueryProvider from "../integrations/tanstack-query/root-provider";
import appCss from "../styles.css?url";

interface MyRouterContext {
  queryClient: QueryClient;
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  head: () => ({
    meta: [
      {
        charSet: "utf-8",
      },
      {
        name: "viewport",
        content: "width=device-width, initial-scale=1",
      },
      {
        title: "Altrua - Conectando quem quer ajudar com quem precisa",
      },
      {
        name: "description",
        content:
          "O Altrua é a plataforma que une ONGs, voluntários e doadores. Encontre causas que importam para você e contribua com seu tempo ou recursos.",
      },
      {
        name: "keywords",
        content:
          "voluntariado, ONGs, doações, causas sociais, voluntários, impacto social, Altrua",
      },
      {
        name: "theme-color",
        content: "#ffffff",
      },
      {
        property: "og:type",
        content: "website",
      },
      {
        property: "og:site_name",
        content: "Altrua",
      },
      {
        property: "og:title",
        content: "Altrua - Conectando quem quer ajudar com quem precisa",
      },
      {
        property: "og:description",
        content:
          "O Altrua é a plataforma que une ONGs, voluntários e doadores. Encontre causas que importam para você e contribua com seu tempo ou recursos.",
      },
      {
        property: "og:locale",
        content: "pt_BR",
      },
      {
        property: "og:image",
        content: "/og.png",
      },
      {
        name: "twitter:card",
        content: "summary_large_image",
      },
      {
        name: "twitter:title",
        content: "Altrua - Conectando quem quer ajudar com quem precisa",
      },
      {
        name: "twitter:description",
        content:
          "O Altrua é a plataforma que une ONGs, voluntários e doadores. Encontre causas que importam para você e contribua com seu tempo ou recursos.",
      },
      {
        name: "twitter:image",
        content: "/og.png",
      },
    ],
    links: [
      {
        rel: "stylesheet",
        href: appCss,
      },
    ],
  }),
  shellComponent: RootDocument,
});

function RootDocument({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR" suppressHydrationWarning>
      <head>
        <HeadContent />
      </head>
      <body className="wrap-anywhere font-sans antialiased">
        <TanStackQueryProvider>
          <div className="flex min-h-dvh flex-col gap-20">
            <Header />
            <main className="flex-1">{children}</main>
            <Footer />
          </div>

          <TanStackDevtools
            config={{
              position: "bottom-right",
            }}
            plugins={[
              {
                name: "Tanstack Router",
                render: <TanStackRouterDevtoolsPanel />,
              },
              TanStackQueryDevtools,
            ]}
          />
        </TanStackQueryProvider>
        <Scripts />
      </body>
    </html>
  );
}
