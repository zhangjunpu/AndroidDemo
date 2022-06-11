package com.junpu.aidl.client.demo;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 自动生成的 AIDL 类，格式化后方便阅读
 * @author junpu
 * @date 2022/6/10
 */
public interface IMyAidlInterface extends IInterface {
    /**
     * Default implementation for IMyAidlInterface.
     */
    public static class Default implements IMyAidlInterface {
        @Override
        public int add(int x, int y) throws RemoteException {
            return 0;
        }

        @Override
        public int sub(int x, int y) throws RemoteException {
            return 0;
        }

        @Override
        public int mul(int x, int y) throws RemoteException {
            return 0;
        }

        @Override
        public int div(int x, int y) throws RemoteException {
            return 0;
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends Binder implements IMyAidlInterface {
        private static final String DESCRIPTOR = "com.junpu.aidl.service.IMyAidlInterface";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.junpu.aidl.service.IMyAidlInterface interface,
         * generating a proxy if needed.
         */
        public static IMyAidlInterface asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            // 不为空说明是同一进程，直接返回 Stub 实体
            if (((iin != null) && (iin instanceof IMyAidlInterface))) {
                return ((IMyAidlInterface) iin);
            }
            // 不同进程返回 Proxy
            return new IMyAidlInterface.Stub.Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_add: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.add(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_sub: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.sub(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_mul: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.mul(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_div: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _result = this.div(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IMyAidlInterface {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public int add(int x, int y) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_add, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().add(x, y);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int sub(int x, int y) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_sub, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().sub(x, y);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int mul(int x, int y) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_mul, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().mul(x, y);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int div(int x, int y) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_div, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().div(x, y);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public static IMyAidlInterface sDefaultImpl;
        }

        static final int TRANSACTION_add = (IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_sub = (IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_mul = (IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_div = (IBinder.FIRST_CALL_TRANSACTION + 3);

        public static boolean setDefaultImpl(IMyAidlInterface impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Stub.Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IMyAidlInterface getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    public int add(int x, int y) throws RemoteException;

    public int sub(int x, int y) throws RemoteException;

    public int mul(int x, int y) throws RemoteException;

    public int div(int x, int y) throws RemoteException;
}
