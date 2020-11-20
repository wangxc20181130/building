package com.sancaijia.building.core.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class IdWorker {
    private static final long TW_EPOCH = 1351101713L;
    private static final long WORKER_ID_BITS = 2L;
    private static final long DATA_CENTER_ID_BITS = 2L;
    private static final long MAX_WORKER_ID = 3L;
    private static final long MAX_DATA_CENTER_ID = 3L;
    private static final long SEQUENCE_BITS = 15L;
    private static final long WORKER_ID_SHIFT = 15L;
    private static final long DATA_CENTER_ID_SHIFT = 17L;
    private static final long TIMESTAMP_LEFT_SHIFT = 19L;
    private static final long SEQUENCE_MASK = 32767L;
    private static long lastTimestamp = -1L;
    private long sequence = 0L;
    private final long workerId;
    private final long dataCenterId;

    public IdWorker() {
        this.dataCenterId = getDataCenterId(3L);
        this.workerId = getMaxWorkerId(this.dataCenterId, 3L);
    }

    public IdWorker(long workerId, long dataCenterId) {
        if (workerId <= 3L && workerId >= 0L) {
            if (dataCenterId <= 3L && dataCenterId >= 0L) {
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", 3L));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", 3L));
        }
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        } else {
            if (lastTimestamp - timestamp == 0L) {
                this.sequence = this.sequence + 1L & 32767L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            lastTimestamp = timestamp;
            return timestamp - 1351101713L << 19 | this.dataCenterId << 17 | this.workerId << 15 | this.sequence;
        }
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis() / 1000L;
    }

    protected static long getMaxWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            buffer.append(name.split("@")[0]);
        }

        return (long)(buffer.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
    }

    private static long getDataCenterId(long maxDataCenterId) {
        long id = 0L;

        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = (255L & (long)mac[mac.length - 1] | 65280L & (long)mac[mac.length - 2] << 8) >> 6;
                id %= maxDataCenterId + 1L;
            }
        } catch (Exception e) {
            System.out.println(" getDataCenterId: " + e.getMessage());
        }

        return id;
    }
}
