package contract.rules.citation;

import contract.rules.enums.RuleSource;

import java.util.List;

import static contract.rules.enums.RuleSource.*;
import static java.util.Arrays.asList;

public class AllowedCitationLink {

    public static final List<AllowedCitationLink> ALLOWED_CITATIONS = asList(
            new AllowedCitationLink(CRG, CR),
            new AllowedCitationLink(CR, ANY_DOCUMENT),
            new AllowedCitationLink(MTR, JAR),
            new AllowedCitationLink(IPG, JAR),
            new AllowedCitationLink(MTR, IPG)
    );

    private RuleSource inboundCitation;
    private RuleSource outboundCitation;

    private AllowedCitationLink(RuleSource inbound, RuleSource outbound) {
        this.inboundCitation = inbound;
        this.outboundCitation = outbound;
    }

    public static boolean isAllowed(RuleSource inbound, RuleSource outbound) {
        return inbound == outbound ||
                ALLOWED_CITATIONS.stream()
                        .anyMatch(citationLink ->
                                (citationLink.inboundCitation == inbound || citationLink.inboundCitation == ANY_DOCUMENT)
                                        && (citationLink.outboundCitation == outbound || citationLink.outboundCitation == ANY_DOCUMENT)
                        );
    }
}
