import Image from "next/image";
import React, { useState } from "react";
import { Flex, Popover } from "antd";
import { HeartFilled, HeartOutlined } from "@ant-design/icons";
import img1 from "@/asset/images/img1.png";

export default function Card({ active, data, onClick = () => {} }) {
  return (
    <>
      <div
        onClick={onClick}
        className="cursor-pointer hover:shadow-xl p-2 rounded-2xl"
      >
        <div className="w-full h-48 flex justify-center items-center rounded-lg">
          <Image
            className="w-full h-48 rounded-lg"
            width={120}
            height={120}
            src={JSON.parse(data.image)?.[0] || img1}
            alt="image"
          />
        </div>
        <Flex gap={2} justify="space-between" align="center" className="mt-2">
          <p className="font-semibold text-lg"> {data.roomNumber}</p>
        </Flex>
        <p>
          {data.price.toLocaleString("en-US")}đ{" "}
          <span className="text-sm">/month</span>
        </p>
        <p
          className={`text-sm font-medium mt-1 ${
            data.status === "RENTED" ? "text-red-500" : "text-green-500"
          }`}
        >
          {data.status === "RENTED" ? "Rented" : "Available"}
        </p>
      </div>
    </>
  );
}
