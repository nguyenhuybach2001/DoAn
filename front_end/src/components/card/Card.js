import Image from "next/image";
import React from "react";
import img1 from "@/asset/images/img1.png";
import { Flex } from "antd";
import { HeartFilled, HeartOutlined } from "@ant-design/icons";

export default function Card({ active }) {
  return (
    <div className="cursor-pointer hover:shadow-xl p-2 rounded-2xl">
      <div>
        <Image src={img1} alt="image" />
      </div>
      <Flex gap={2} justify="space-between" align="center" className="mt-2">
        <p className="font-semibold text-lg"> Amall House</p>
        {active ? (
          <HeartFilled className="rounded-full border-2 border-gray-300 p-1 text-blue-500 cursor-pointer" />
        ) : (
          <HeartOutlined className="rounded-full border-2 border-gray-300 p-1 text-blue-500 cursor-pointer" />
        )}
      </Flex>
      <p>
        {(20000).toLocaleString("en-US")}{" "}
        <span className="text-sm">/month</span>
      </p>
      <p>Rental</p>
    </div>
  );
}
