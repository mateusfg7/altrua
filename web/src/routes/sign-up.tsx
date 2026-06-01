import { createFileRoute } from "@tanstack/react-router";
import { AuthLayout } from "~/components/shell/layout/auth-layout";
import { SignUpForm } from "~/components/signup-form";

export const Route = createFileRoute("/sign-up")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <AuthLayout>
      <SignUpForm />
    </AuthLayout>
  );
}
