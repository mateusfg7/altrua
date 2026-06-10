import { createFileRoute, Outlet } from "@tanstack/react-router";
import { Footer } from "~/components/shell/footer";
import { Header } from "~/components/shell/header";
import { AppSidebar } from "~/components/shell/sidebar";
import { profileQueryOptions } from "~/hooks/use-profile";
import { useAuthStore } from "~/store/auth.store";

export const Route = createFileRoute("/_auth")({
  beforeLoad: async ({ context }) => {
    const accessToken = useAuthStore.getState().accessToken;
    if (accessToken) {
      await context.queryClient.ensureQueryData(profileQueryOptions);
    }
    return { isAuthenticated: !!accessToken };
  },
  component: RouteComponent,
});

function RouteComponent() {
  const { isAuthenticated } = Route.useRouteContext();

  if (isAuthenticated) {
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
