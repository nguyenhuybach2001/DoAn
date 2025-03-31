"use client";
import { Avatar, Button, Dropdown, Form, Input, Modal } from "antd";
import Image from "next/image";
import React, { useEffect, useState } from "react";
import logo from "@/asset/images/logo.png";
import { usePathname, useRouter } from "next/navigation";
import { useDispatch, useSelector } from "react-redux";
import { closeLogin, openLogin } from "@/src/redux/slices/modalSlice";
import authApi from "@/src/api/authApi";
import { addUser } from "@/src/redux/slices/authSlice";
import { UserOutlined } from "@ant-design/icons";

export default function Header() {
  const pathName = usePathname();
  const router = useRouter();
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const [step, setStep] = useState(0);
  const [modalUser, setModalUser] = useState(false);
  const { modalLogin } = useSelector((state) => state.modal);
  const { user } = useSelector((state) => state.auth);
  const [countdown, setCountdown] = useState(0);
  const [isCounting, setIsCounting] = useState(false);
  const [errorMess, setErrorMess] = useState("");

  useEffect(() => {
    let timer;
    if (step === 2 && isCounting) {
      timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            setIsCounting(false);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isCounting, step]); // Chạy lại khi step hoặc isCounting thay đổi
  useEffect(() => {
    const fetchLogin = async () => {
      const user_token = localStorage.getItem("accessToken");
      if (user_token) {
        const responsive = await authApi.getUserInfo();
        if (responsive) {
          dispatch(closeLogin());
          dispatch(addUser(responsive));
        }
      }
    };
    fetchLogin();
  }, []);
  useEffect(() => {
    if (step === 2) {
      setCountdown(30);
      setIsCounting(true); // Bắt đầu countdown ngay khi vào step 2
    }
  }, [step]);
  const showModal = () => {
    dispatch(openLogin());
  };
  const handleCancel = () => {
    dispatch(closeLogin());
    form.resetFields();
  };
  const items = [
    {
      label: (
        <p
          onClick={() => {
            setModalUser(true);
          }}
        >
          Xem thông tin
        </p>
      ),
      key: "0",
    },
    {
      label: (
        <p
          onClick={() => {
            dispatch(addUser(null));
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
          }}
        >
          Log out
        </p>
      ),
      key: "1",
    },
  ];
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

  const onLogin = async (values) => {
    const res = await authApi.login(values.username, values.password);
    if (res) {
      const responsive = await authApi.getUserInfo();
      if (responsive) {
        dispatch(closeLogin());
        dispatch(addUser(responsive));
        form.resetFields();
      }
    }
  };
  const onRegister = async (values) => {
    const res = await authApi.createAccount(values.email);
    if (res.status == "true") {
      setStep(2);
    } else {
      setErrorMess(res.message);
    }
  };
  const handleResend = () => {
    setCountdown(30);
    setIsCounting(true);
  };
  const renderModal = () => {
    switch (step) {
      case 0:
        return (
          <Form form={form} name="basic" onFinish={onLogin} className="px-4">
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
            <Button
              className="text-black font-bold w-full cursor-pointer"
              onClick={() => setStep(1)}
            >
              Register
            </Button>
          </Form>
        );
      case 1:
        return (
          <Form form={form} onFinish={onRegister} className="px-4">
            <p className="text-3xl font-bold my-6">Register</p>
            <Form.Item
              label={null}
              name="email"
              rules={[
                {
                  validator: (_, value) => {
                    if (!value) {
                      return Promise.reject("Please input your email!");
                    }
                    if (errorMess) {
                      return Promise.reject(errorMess);
                    }
                    // const phoneRegex = /^[0-9]{10,11}$/;
                    // if (/^\d+$/.test(value)) {
                    //   return phoneRegex.test(value)
                    //     ? Promise.resolve()
                    //     : Promise.reject("Invalid phone number format!");
                    // }

                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    return emailRegex.test(value)
                      ? Promise.resolve()
                      : Promise.reject("Invalid email format!");
                  },
                },
              ]}
            >
              <Input placeholder="Join us today! Enter your email to register" />
            </Form.Item>
            <Form.Item label={null}>
              <Button className="w-full" type="primary" htmlType="submit">
                Continue
              </Button>
            </Form.Item>
            <p className="text-black">
              Already have an account?{" "}
              <span
                onClick={() => setStep(0)}
                className="text-blue-500 font-bold border-b-[1px] border-blue-500 w-fit cursor-pointer"
              >
                Login
              </span>
            </p>
          </Form>
        );
      case 2:
        return (
          <div>
            <p className="text-3xl font-bold my-6">Registration Successful</p>
            <p>
              We have sent a confirmation email to
              <span className="text-blue-500">
                {" "}
                {form.getFieldValue("email")}
              </span>
              .{" "}
            </p>
            <p>
              Please check your inbox and follow the instructions to complete
              your registration.
            </p>
            <p>Didn't receive the email? </p>
            <div className="flex gap-3">
              <Button
                onClick={() => {
                  handleResend();
                }}
                className="w-32"
                disabled={countdown > 0}
              >
                {countdown > 0 ? `${countdown}s` : "Resend Email"}
              </Button>
              <Button
                onClick={() => {
                  setStep(1);
                }}
              >
                Change Email
              </Button>
            </div>
            <p>
              Already verified?{" "}
              <span
                className="text-blue-500 font-bold border-b-[1px] border-blue-500 w-fit cursor-pointer"
                onClick={() => {
                  setStep(0);
                  form.setFieldValue("username", form.getFieldValue("email"));
                }}
              >
                Login here
              </span>
            </p>
          </div>
        );
      default:
        break;
    }
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
      {user ? (
        <Dropdown menu={{ items }} trigger={["click"]}>
          <div className="flex gap-2 items-center cursor-pointer">
            <p>{user.fullName}</p>
            <Avatar icon={<UserOutlined />} />
          </div>
        </Dropdown>
      ) : (
        <Button onClick={showModal}>Log in</Button>
      )}
      <Modal
        open={modalLogin}
        footer={false}
        width={500}
        onCancel={handleCancel}
      >
        {renderModal()}
      </Modal>
      <Modal
        open={modalUser}
        footer={false}
        onCancel={() => {
          setModalUser(false);
        }}
      >
        <div>{user?.fullName}</div>
      </Modal>
    </div>
  );
}
