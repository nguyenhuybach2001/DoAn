"use client";
import { makeStore } from "@/src/redux/store";
import { useRef } from "react";
import { Provider } from "react-redux";

export default function StoreProvider({ children }) {
  const storeRef = useRef();

  if (!storeRef.current) {
    storeRef.current = makeStore();
  }

  return (
    <>
      <Provider store={storeRef.current}>{children}</Provider>
    </>
  );
}
