#!/bin/bash

gateway=10.0.3.119
clientA=10.0.5.2
clientB=10.0.5.3
serverA=10.0.4.2
serverB=10.0.4.3

# First, reset all the rules to the default values.
echo "Restoring the default rules"
iptables-restore < /root/defaultFirewall.rules
# Set the rules for us to be able to SSH into all the machines.


echo "First: allow SSH to the gateway."
iptables -A INPUT  -i eth0 -p tcp --dport 22 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
iptables -A OUTPUT -o eth0 -p tcp --sport 22 -m conntrack --ctstate ESTABLISHED -j ACCEPT


echo "R1) Clients A and B can both access the websites hosted on Servers A and B."
# --> what comes in from br1, and is headed to br0 with a port value of 80 or 443 (HTTPS) is ALLOWED.
# incoming traffic from br1 to br0 with tcp protocol, and ports 80 or 443, that are either new packets or established packets, are accepted.
iptables -A FORWARD -i br1 -o br0 -p tcp -m multiport --dports 80,443 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
# incoming traffic from br0 to br1 with tcp protocol, and ports 80 or 443, that are established packets, are accepted. (server can't initiate a http to client)
iptables -A FORWARD -i br0 -o br1 -p tcp -m multiport --sports 80,443 -m conntrack --ctstate ESTABLISHED -j ACCEPT

echo "R2) Client A can SSH to both Servers A and B."
# what comes in from br1, with IP address 10.0.5.2, and port 22, and is going to bro, with a destination port of 22, is ALLOWED.
iptables -A FORWARD -i br1 -o br0 -p tcp -s $clientA --dport 22 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i br0 -o br1 -p tcp -d $clientA --sport 22 -m conntrack --ctstate ESTABLISHED -j ACCEPT

echo "R3) Client B cannot SSH to either Server."
# What comes in from br1, with IP address 10.0.5.3, and port 22, and is going to bro is DROPPED
iptables -A FORWARD -s $clientB -o br0 -p tcp --dport 22 -j DROP

echo "R4) All traffic going to UDP port 123 on the Gateway should be forwarded to the same port on Server B. (So, any host in the network using the NTP service should connect to UDP port 123 on the Gateway rather than going dire
ctly to Server B.)"
# What comes in with protocol udp and destination port 123 and destination of <this machine>, gets redirected to a destination address of 10.0.4.3 (Server B)
iptables -t nat -A PREROUTING -p udp --dport 123 -j DNAT --to-destination $serverB:123
iptables -A FORWARD -d $serverB -p udp --dport 123 -j ACCEPT
iptables -A FORWARD -s $serverB -p udp --dport 123 -j ACCEPT
iptables -t nat -A POSTROUTING -p udp --dport 123 -j SNAT --to-source $gateway

echo "R5) Client A is a “secure” client that can only access services running on Servers A and B and cannot access the world outside your group’s virtual machine; i.e., Client A’s traffic is not forwarded outside the firewall to other group’s or to the WAN."
# What comes in from Client A and has a destination other than Servers A and B is dropped.
iptables -A FORWARD -s $clientA -o br0 -j ACCEPT
iptables -A FORWARD -i br0 -d $clientA -j ACCEPT
iptables -A FORWARD -s $clientA -o eth0 -j DROP

echo "R6) Client B can access services running on Servers A and B as well as devices in the “outside world” (other groups’ virtual machines, and the public WAN)."
iptables -A FORWARD -s $clientB -j ACCEPT
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
iptables -A FORWARD -d $clientB -j ACCEPT

echo "R7) The website hosted on Server A should be accessible outside the firewall and should appear to be running on the Gateway. In other words, connections to TCP port 80 on your Gateway’s external interface (e.g., coming from another group’s Gateway or Client B), should be forwarded to TCP port 80 on Server A. We recommend implementing and testing this requirement last, after you have the others working. You will need the help of another group or the course staff to test that this requirement is working correctly."
# TODO: Finish this and test with another group.
# Whatever comes in with a destination address-
iptables -t nat -A PREROUTING  -d $gateway -p tcp --dport 80 -j DNAT --to-destination $serverA
iptables -t nat -A POSTROUTING -s $serverA   -p tcp --sport 80 -j SNAT --to-source $gateway
iptables -A FORWARD -d $serverA -p tcp --dport 80 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
iptables -A FORWARD -s $serverA -p tcp --sport 80 -m conntrack --ctstate ESTABLISHED -j ACCEPT

echo "R8) The website hosted on Server B should only be accessible by nodes inside your firewall (Clients A and B, and Server A)."
# TODO: Need to test with another group that they cannot access server B's website. I have already tested that all nodes in our network can access it.
# For Client A and Client B traffic.
iptables -A FORWARD -i br1 -o br0 -d $serverB -p tcp -m multiport --dports 80,443 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i br0 -o br1 -s $serverB -p tcp -m multiport --sports 80,443 -m conntrack --ctstate ESTABLISHED -j ACCEPT
# For Server A traffic.
iptables -A FORWARD -s $serverA -d $serverB -p tcp -m multiport --dports 80,443 -m conntrack --ctstate NEW,ESTABLISHED -j ACCEPT
iptables -A FORWARD -s $serverB -d $serverA -p tcp -m multiport --sports 80,443 -m conntrack --ctstate ESTABLISHED -j ACCEPT
# Drop everything else (i.e. anything coming from eth0).
iptables -A FORWARD -i eth0 -d $serverB -p tcp -m multiport --dports 80,443 -m conntrack --ctstate NEW,ESTABLISHED -j DROP

iptables -A INPUT   -j LOG
iptables -A OUTPUT  -j LOG
iptables -A FORWARD -j LOG

iptables -P INPUT ACCEPT
iptables -P OUTPUT ACCEPT
iptables -P FORWARD DROP
