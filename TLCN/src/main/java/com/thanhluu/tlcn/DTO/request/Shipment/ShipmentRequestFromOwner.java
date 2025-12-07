package com.thanhluu.tlcn.DTO.request.Shipment;

import com.thanhluu.tlcn.Enum.GHN.RequiredNote;
import lombok.Data;

@Data
public class ShipmentRequestFromOwner {
  // Ordernumber , fromAdrress, requirement Note
  private String orderNumber;
  private String senderName;
  private String senderPhone;
  private String senderAddress;
  private Integer height;
  private Integer weight;
  private Integer length;
  private Integer width;
  private RequiredNote requiredNote;
}
