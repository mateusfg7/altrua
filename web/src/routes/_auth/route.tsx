import { createFileRoute, Outlet } from "@tanstack/react-router";
import { Footer } from "~/components/shell/footer";
import { Header } from "~/components/shell/header";
import { AppSidebar } from "~/components/shell/sidebar";
import { useAuthStore } from "~/store/auth.store";

export const Route = createFileRoute("/_auth")({
  component: RouteComponent,
});

function RouteComponent() {
  const accessToken = useAuthStore((s) => s.accessToken);

  // Authenticated users get the app shell: a fixed left sidebar instead of the
  // marketing topbar/footer used on the public landing page.
  if (accessToken) {
    return (
      <div className="flex min-h-dvh">
        <AppSidebar />
        <main className="min-w-0 flex-1">
          <Outlet />
        </main>
      </div>
    );
  }

  return (
    <div className="flex min-h-dvh flex-col gap-20">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
