"use client";
import { Button, Form, Input, Modal } from "antd";
import Image from "next/image";
import React, { useEffect, useState } from "react";
import logo from "@/asset/images/logo.png";
import { usePathname, useRouter } from "next/navigation";
import { useDispatch, useSelector } from "react-redux";
import { closeLogin, openLogin } from "@/src/redux/slices/modalSlice";

export default function Header() {
  const pathName = usePathname();
  const router = useRouter();
  const dispatch = useDispatch();
  const { modalLogin } = useSelector((state) => state.modal);
  const showModal = () => {
    dispatch(openLogin());
  };
  const handleCancel = () => {
    dispatch(closeLogin());
  };
  const listTab = [
    {
      name: "Home",
      active: pathName === "/home",
    },
    {
      name: "Dashboard",
      active: pathName === "/dashboard",
    },
    {
      name: "About us",
      active: pathName === "/about",
    },
  ];
  const onFinish = (values) => {
    console.log("Success:", values);
  };
  const onFinishFailed = (errorInfo) => {
    console.log("Failed:", errorInfo);
  };
  return (
    <div className="sticky top-0 flex justify-between items-center p-4 bg-white shadow-md px-16 z-10">
      <Image
        src={logo}
        alt="logo"
        onClick={() => router.push("/home")}
        className="w-16 cursor-pointer"
      />
      <div className=" flex gap-5 justify-center items-center">
        {listTab.map((tab) => (
          <p
            key={tab.name}
            className={`${
              tab.active
                ? "text-blue-500 border-b-2 border-blue-500"
                : "text-black"
            } cursor-pointer font-bold`}
          >
            {tab.name}
          </p>
        ))}
      </div>
      <Button onClick={showModal}>Log in</Button>
      <Modal
        open={modalLogin}
        footer={false}
        width={300}
        onCancel={handleCancel}
      >
        <Form
          name="basic"
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
          className="px-4"
        >
          <p className="text-3xl font-bold my-6">Login</p>
          <Form.Item
            label={null}
            name="username"
            rules={[
              {
                validator: (_, value) => {
                  if (!value) {
                    return Promise.reject(
                      "Please input your email or phone number!"
                    );
                  }

                  const phoneRegex = /^[0-9]{10,11}$/;
                  if (/^\d+$/.test(value)) {
                    return phoneRegex.test(value)
                      ? Promise.resolve()
                      : Promise.reject("Invalid phone number format!");
                  }

                  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                  return emailRegex.test(value)
                    ? Promise.resolve()
                    : Promise.reject("Invalid email format!");
                },
              },
            ]}
          >
            <Input placeholder="Email or Phone number" />
          </Form.Item>

          <Form.Item
            label={null}
            name="password"
            rules={[
              {
                required: true,
                message: "Please input your password!",
              },
            ]}
          >
            <Input.Password placeholder="Password" />
          </Form.Item>
          <Form.Item label={null}>
            <Button className="w-full" type="primary" htmlType="submit">
              Login
            </Button>
            <div className="flex justify-end">
              <p className="text-blue-500 border-b-[1px] border-blue-500 w-fit cursor-pointer">
                Forgot Password
              </p> 
            </div>
          </Form.Item>
          <Button className="text-black font-bold w-full cursor-pointer">
            Register
          </Button>
        </Form>
      </Modal>
    </div>
  );
}
