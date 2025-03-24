import { EnvironmentTwoTone } from "@ant-design/icons";
import { Button, Input } from "antd";
import React from "react";

export default function Search() {
  return (
    <div className="flex gap-2 items-center my-5 w-3/5 border-2 border-[#a3a3a3] p-2 rounded-lg">
      <EnvironmentTwoTone className="text-xl" />
      <Input variant="borderless" placeholder="Search by location..." />
      <Button type="primary">Search</Button>
    </div>
  );
}
