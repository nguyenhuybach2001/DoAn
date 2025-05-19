import authApi from "@/src/api/authApi";
import DashboardScreen from "@/src/page/dashboard/DashboardScreen";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import React from "react";

export default async function Page() {
  return <DashboardScreen />;
}
