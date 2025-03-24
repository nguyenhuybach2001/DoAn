"use client";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import Search from "@/src/components/search/Search";
import Card from "@/src/components/card/Card";

export default function Home() {
  const dispatch = useDispatch();
  console.log(useSelector((state) => state));
  return (
    <div className="px-16 py-8">
      <h1 className="text-5xl font-bold w-1/2 ">
        Find Your Perfect Rental Home
      </h1>
      <p className="my-5 ">
        An optimized platform to connect you with landlords and find your ideal
        rental space!
      </p>
      <ul>
        <li>🔹 Easily search by location</li>
        <li>🔹 Manage contracts & payments seamlessly</li>
        <li>🔹 Connect directly with landlords—no middleman</li>
      </ul>
      <Search />
      <h2 className="text-3xl font-bold">Popular</h2>
      <div className="grid my-8 sm:grid-cols-2 gap-8 md:grid-cols-3 lg:grid-cols-4">
        {Array.from({ length: 6 }).map((_, index) => (
          <Card />
        ))}
      </div>
     
    </div>
  );
}
