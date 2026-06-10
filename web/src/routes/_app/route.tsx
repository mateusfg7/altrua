import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";
import { AppSidebar } from "~/components/shell/sidebar";
import { profileQueryOptions } from "~/hooks/use-profile";
import { useAuthStore } from "~/store/auth.store";

export const Route = createFileRoute("/_app")({
  beforeLoad: async ({ context }) => {
    const accessToken = useAuthStore.getState().accessToken;
    if (!accessToken) {
      throw redirect({ to: "/" });
    }
    await context.queryClient.ensureQueryData(profileQueryOptions);
    return { isAuthenticated: true };
  },
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="flex min-h-dvh">
      <AppSidebar />
      <main className="min-w-0 flex-1">
        <Outlet />
      </main>
    </div>
  );
}
